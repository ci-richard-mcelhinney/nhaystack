#!/usr/bin/env bash

# Script to use from bash command line to test the reading of an
# EnumWritable in a Niagara Station.  Script must have executable
# permissions set on your file system, e.g.
#
#    chmod a+x override.sh
#
# before trying to run.
#
# You may need to customise the URL and 'id' as necessary to run on your system.
#
# For this script to work you will need to configure your Niagara Station to use
# HTTP instead of HTTPS and you will need to have a user configured for Basic
# Authentication.  The username and password need to be filled in at the appropriate
# location in the command below.
#
# N.B.  THIS SCRIPT IS FOR USE ON LOCAL NETWORKS ONLY, DO NOT USE ON PRODUCTION
#       PRODUCTION SYSTEMS THAT ARE EXPOSED OVER THE INTERNET

curl --location --request GET 'http://localhost/haystack/read?filter=id==@C.Apps.EnumWritable' \
-H "Content-Type: text/zinc; charset=utf-8" \
--user "<username>:<password>"