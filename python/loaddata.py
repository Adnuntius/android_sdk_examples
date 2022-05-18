#!/usr/bin/env python3

import sys
from os import listdir
from os.path import isfile, join
from time import sleep
from PIL import Image
import os
from datetime import timedelta, date, datetime
from adnuntius.api import Api, AdServer
from adnuntius.util import generate_alphanum_id, date_to_string, id_reference
from bs4 import BeautifulSoup
import urllib.parse


network_id = 'network_1'
admin_user = 'broker1@adnuntius.com'
admin_password = 'abc**123'
api_url = 'http://localhost:8079/api'
ad_server_host = 'http://localhost'
ad_server_port = 8078


def retry(what, retries, func, sleep_time=1):
    """
    Executes func for the specified number of times until it stops throwing exceptions.
    Returns the value returned by func.
    """
    result = None
    first = True
    while retries > 0:
        try:
            result = func()
            break
        except:
            if first:
                print("", file=sys.stderr)
                first = False
            if retries == 1:
                print("Too many retries for '" + what + "'", file=sys.stderr)
                raise
        print("Re-trying " + what + "'...", file=sys.stderr)
        retries -= 1
        sleep(sleep_time)

    return result


def validate_served_ad(html, asset):
    soup = BeautifulSoup(html, "html.parser")

    # currently we can't validate the click token url, so just do a basic check
    assert soup.html.body.a is not None, "Html body in wrong format: " + str(soup.html.body)
    assert soup.html.body.a['href'].startswith("http://"), "Html ref in wrong format: " + str(soup.html.body.a['href'])
    assert soup.html.body.a.img['width'] == str(asset["width"]), "Width is incorrect"
    assert soup.html.body.a.img['height'] == str(asset["height"]), "Height is incorrect"
    assert soup.html.body.a.img['src'] == asset["cdnId"], "CdnId is incorrect: Expected %r got %r" % (soup.html.body.a.img['src'], asset["cdnId"])


def check_ad_serving(ad_server, ad_unit_tag, asset, user_agent, version, retries=120, sleep_time=1):
    urlEncodedKeyValue = urllib.parse.quote('[{"version": "' + version + '"}]')
    def check():
        response = ad_server.request_ad_unit(ad_unit_tag['auId'], cookies=None, headers={'User-Agent': user_agent}, extra_params={'kv': urlEncodedKeyValue})
        validate_served_ad(response.text, asset)
    retry("Expecting ad to serve auId=" + ad_unit_tag['auId'], retries, check, sleep_time)


def check_creatives_serving(ad_server, ad_unit_tag, assets):
    iphone_useragent = "Mozilla/5.0 (iPhone; CPU iPhone OS 15_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.1 Mobile/15E148 Safari/604.1"
    android_useragent = "Mozilla/5.0 (Linux; Android 11; Android SDK built for x86 Build/RSR1.210210.001.A1; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/83.0.4103.106 Mobile Safari/537.36"

    for asset in assets:
        print("Checking asset " + asset['fileName'] + "...")
        os, version, _ = get_image_filename_info(asset['fileName'])
        if os == 'android':
            user_agent = android_useragent
        else: # os == 'iphone'
            user_agent = iphone_useragent
        check_ad_serving(ad_server, ad_unit_tag, asset, user_agent, version)


def create_sdk_site_ad_unit(api, suffix):
    team = api.teams.get('defaultteam')

    site = api.sites.update({
        'id': generate_alphanum_id(),
        'name': 'SdkSite ' + suffix,
        'teams': [team['id']]
    })

    render_template = '<a href="{{{urls.destination.url}}}"><img src="{{{assets.image.cdnId}}}" width="{{assets.image.width}}" height="{{assets.image.height}}" style="width:{{assets.image.width}}px; height:{{assets.image.height}}px"/></a>'
    layout = api.layouts.update({
        'id': generate_alphanum_id(),
        'name': 'SdkLayout ' + suffix,
        'renderTemplate': render_template,
        'layoutComponents': [{
            'type': 'ASSET',
            'tag': 'image',
            'mimeTypes': ['IMAGE_JPEG', 'IMAGE_PNG', 'IMAGE_GIF'],
            'maxFileSizeBytes': 40000
        }, {
            'type': 'URL',
            'tag': 'destination',
            'protocol': 'https'
        }]
    })

    ad_unit = api.ad_units.update({
        'id': generate_alphanum_id(),
        'name': 'SDKAdUnit ' + suffix,
        'width': '320',
        'height': '480',
        'pageSize': '1',
        'site': site['id']
    })

    ad_unit_tag = api.ad_unit_tags.get(ad_unit['id'])

    return site, ad_unit, ad_unit_tag, team, layout


def get_assets_path():
    script_path = os.path.dirname(os.path.realpath(__file__))
    return os.path.abspath(script_path + "/assets/")


def get_image_filenames():
    assets_dir = get_assets_path()
    image_filenames = [f for f in listdir(assets_dir) if isfile(join(assets_dir, f))]
    return image_filenames


def get_image_filename_info(image_filename):
    base_image_filename, _ = os.path.splitext(image_filename)

    device_targeting = '-no-device-targeting' not in base_image_filename
    if 'android' in base_image_filename:
        target_os = 'android'
        version = base_image_filename.replace('android-', '').replace('-no-device-targeting', '')
        if version == 'android':
            version = 'unspecified'

    elif 'iphone' in base_image_filename:
        target_os = 'iphone'
        version = base_image_filename.replace('iphone-', '').replace('-no-device-targeting', '')
        if version == 'iphone':
            version = 'unspecified'
    return target_os, version, device_targeting


def get_image_file_info(image_filename):
    assets_dir = get_assets_path()
    image_file = assets_dir + "/" + image_filename
    img = Image.open(image_file)
    width, height = img.size

    target_os, version, device_targeting = get_image_filename_info(image_filename)
    base_image_filename, _ = os.path.splitext(image_filename)
    name = 'Sdk' + base_image_filename.replace('-', ' ').title().replace(' ', '')
    return image_file, 'image/jpeg', name, target_os, version, device_targeting, width, height


def create_sdk_creative(api, lineitem, layout, image_filename):
    image_file, mime_type, name, target_os, version, device_targeting, width, height = get_image_file_info(image_filename)

    creative = api.creatives.update({
        'id': generate_alphanum_id(),
        'name': name,
        'lineItem': lineitem['id'],
        'layout': layout['id'],
        'width': width,
        'height': height,
        'constraintsToUrls': {
            'destination': 'https://github.com/Adnuntius/android_sdk'
        }
    })

    asset = api.assets.upload_resource(creative['id'], generate_alphanum_id(), image_file, mime_type)

    creative['constraintsToAssets'] = {
        layout['layoutComponents'][0]['tag']: asset['id']
    }

    creative['targeting'] = {}
    if target_os == 'android' and device_targeting:
        creative['targeting']['deviceTargets'] = {'targetedOSes': ['ANDROID']}
    elif target_os == 'iphone' and device_targeting:
        creative['targeting']['deviceTargets'] = {'targetedOSes': ['IOS']}

    creative['targeting']['keyValueTargets'] = {
        "entries": {"version": [version]}
    }

    api.creatives.update(creative)
    return asset


def setup_sdk_domain():
    api = Api(admin_user, admin_password, location=api_url)
    api.defaultArgs['context'] = network_id

    suffix = generate_alphanum_id(4)

    site, ad_unit, ad_unit_tag, team, layout = create_sdk_site_ad_unit(api, suffix)

    order = api.orders.update({
            'id': generate_alphanum_id(),
            'name': 'SdkOrder ' + suffix,
            'team': team['id']})

    lineitem = api.line_items.update({
            'id': generate_alphanum_id(),
            'name': 'SdkLineItem ' + suffix,
            'userState': 'APPROVED',
            'order': order['id'],
            'startDate': date_to_string(datetime.today() - timedelta(hours=23)),
            'bidSpecification': {"cpm": {"currency": "USD", "amount": 1}},
            'targeting': {
                'adUnitTarget': {
                        'adUnits': [ad_unit['id']]
                }
            }
    })

    image_filenames = get_image_filenames()
    assets = []
    for image_filename in image_filenames:
        asset = create_sdk_creative(api, lineitem, layout, image_filename)
        assets.append(asset)

    return site, order, site, ad_unit_tag, assets


if __name__ == '__main__':
    site, order, site, ad_unit_tag, assets = setup_sdk_domain()
  
    print("Test that Ad Unit " + ad_unit_tag['auId'] + " is serving ads ...")
    ad_server = AdServer(ad_server_host, port=ad_server_port)
    check_creatives_serving(ad_server, ad_unit_tag, assets)
    
    
