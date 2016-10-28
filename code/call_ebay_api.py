import urllib
import urllib2
import json
import sys
import os.path

keywords = sys.argv[1]
print(sys.argv[1])

# Put your appname in a json config file. The file will have an entry like:
#   { "appname" : "your-app-name" }
#
my_ebay_appname = ''
with open(os.path.expanduser('.ebay_developer_api_config.json')) as json_file:
    config_json = json.load(json_file)
    my_ebay_appname = config_json['appname']

if not my_ebay_appname:
    sys.exit("Error: No 'appname' entry in .ebay_developer_api_config.json")

finding_service_url = 'https://svcs.ebay.com/services/search/FindingService/v1?'
finding_service_params = { 'OPERATION-NAME' : 'findItemsByKeywords',
                           'SERVICE-VERSION' : '1.0.0',
                           'RESPONSE-DATA-FORMAT' : 'JSON',
                           'keywords' : keywords,
                           'SECURITY-APPNAME' : my_ebay_appname }

req = urllib2.Request(finding_service_url + urllib.urlencode(finding_service_params))
response = urllib2.urlopen(req)
response_json = json.loads(response.read())

# write output to json file, added by linhong
directory = 'searchout'
if not os.path.exists(directory):
    os.makedirs(directory)

file_name = keywords.lower() + '.json'
with open(os.path.join(directory, file_name), 'w') as outfile:
    json.dump(response_json, outfile)
