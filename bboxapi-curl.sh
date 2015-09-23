#!/bin/bash

##################################################################################
# The MIT License (MIT)
#
# Copyright (c) 2015 Bertrand Martel
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
# THE SOFTWARE.
###################################################################################
#title         : bboxapi-curl.sh
#author		   : Bertrand Martel
#date          : 09/09/2015
#description   : bbox api reverse
############################################################################

function test_success {
	echo -e "\x1B[01;32m[SUCCESS] $1 \x1B[0m"
}

function test_error {
	echo -e "\x1B[31m[FAILURE] $1 \x1B[0m"
}

if [ "$1" == "" ] ;then
	echo "You must provide bbox management interface password"
	exit 0
fi

#login for access to private api
request_headers=`curl -sk -D - http://gestionbbox.lan/api/v1/login -X "POST" -d 'password='$1'&remember=1' | grep  'Set-Cookie: '`

cookie_header=`echo -ne "$request_headers" | sed -e 's/Set-Cookie: //g' | sed -e 's/\r//g' | sed -e 's/\n//g' | sed -e 's/\r\n//g'`

token=`echo -ne "$cookie_header" | sed -e 's/;.*//g' | sed -e 's/BBOX_ID=//g'`

if [ "$token" == "" ]; then
	test_error "Authentication"
	exit 0
fi

test_success "Authentication"
echo -e "\x1B[01;32m\tBbox token :\n\t\t$token\x1B[0m"

cookie_header="Cookie: $cookie_header"

#voip api (require login)
voip_result=`curl -sk http://gestionbbox.lan/api/v1/voip -X "GET" --header "${cookie_header}"`

if [ "$voip_result" == "" ]; then
	test_error "Voip API"
	exit 0
fi

test_success "Voip API"
echo -e "\x1B[01;32m\tVoip json :\n\t\t$voip_result\x1B[0m"

if [ "$2" == "" ]; then
	echo "enter phone number for voip dial test"
	exit 0
fi

#voip dial
voip_dial=`curl -ks -D - http://gestionbbox.lan/api/v1/voip/dial -X "PUT" --header "${cookie_header}" -d 'line=1&number='$2'' | grep "200 OK"`

if [ "$voip_dial" == "" ]; then
	test_error "Voip phone dial"
	exit 0
fi

test_success "Voip phone dial"