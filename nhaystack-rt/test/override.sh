#!/usr/bin/env bash

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
eempty
N'