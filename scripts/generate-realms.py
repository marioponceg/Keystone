#!/usr/bin/env python3
"""Generate core-data realm snapshots from Raider.IO's realm listing.

One-time / occasional dev tool. NOT wired into the Gradle build and NOT shipped, and
NOT used at runtime — the app only ever calls Raider.IO's documented v1 API. This uses
Raider.IO's (undocumented) `connected-realms?realm=all` listing purely to produce the
committed static JSON; realm names/slugs are public and change ~yearly.

Run: python3 scripts/generate-realms.py
Writes core-data/src/main/resources/realms/{eu,us,kr,tw}.json as sorted [{name,slug}].
No credentials required.
"""
import json
import pathlib
import urllib.request

REGIONS = ["eu", "us", "kr", "tw"]
OUT = pathlib.Path(__file__).resolve().parent.parent / "core-data/src/main/resources/realms"


def realms(region):
    url = f"https://raider.io/api/connected-realms?region={region}&realm=all"
    # Raider.IO rejects the default Python user-agent; identify as a normal client.
    req = urllib.request.Request(url, headers={"User-Agent": "Keystone-realm-generator"})
    data = json.load(urllib.request.urlopen(req))
    groups = data["realmListing"]["realms"]
    seen, items = set(), []
    for group in groups:
        for realm in group.get("connectedRealms", []):
            slug = realm["slug"]
            if slug not in seen:
                seen.add(slug)
                items.append({"name": realm["name"], "slug": slug})
    return sorted(items, key=lambda r: r["name"].lower())


def main():
    OUT.mkdir(parents=True, exist_ok=True)
    for region in REGIONS:
        data = realms(region)
        (OUT / f"{region}.json").write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n")
        print(f"{region}: {len(data)} realms")


if __name__ == "__main__":
    main()
