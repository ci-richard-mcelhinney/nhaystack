#!/usr/bin/env bash

# Script to use from bash command line to test the override function on a
# NumericWritable in a Niagara Station.  Script must have executable
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
# location in the commands below.
#
# N.B.  THIS SCRIPT IS FOR USE ON LOCAL NETWORKS ONLY, DO NOT USE ON PRODUCTION
#       PRODUCTION SYSTEMS THAT ARE EXPOSED OVER THE INTERNET



curl --location --request POST 'http://localhost/haystack/invokeAction' \
-H "Content-Type: text/zinc; charset=utf-8" \
--user "<your_user>:<your_password>" \
--verbose \
--data-raw 'ver:"3.0" action:"override" id:@C.Apps.NumericWritable
duration,value
60sec,100'

sleep 10s

curl --location --request POST 'http://localhost/haystack/invokeAction' \
-H "Content-Type: text/zinc; charset=utf-8" \
--user "<your_user>:<your_password>" \
--verbose \
--data-raw 'ver:"3.0" action:"auto" id:@C.Apps.NumericWritable
empty
N'