# Local Dev Environment

You could theoretically use this against dev, staging or production, providing the correct credentials
by modifying the script.   However out of the box this script is for a local adnuntius environment.

So this is really only relevant to adnuntius developers as we will not provide access to a local 
environment for anyone else.

## Android Emulator

## For the CDN urls to work, when loading Ads from Android Emulator when using the andemu env, you need to overwrite the CDN base url config

ng.cdn.in.memory.host=10.0.2.2

## Python Requirements

```
python3 -m pip install -r requirements.txt
```

## Existing Ad Units

You can pass `--query` to `loaddata.py` to query for existing SDK Ad Units
