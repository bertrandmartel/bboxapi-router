# BboxApi Router client library #

[![Build Status](https://travis-ci.org/bertrandmartel/bboxapi-router.svg)](https://travis-ci.org/bertrandmartel/bboxapi-router)
[![Download](https://api.bintray.com/packages/bertrandmartel/maven/bboxapi-router/images/download.svg) ](https://bintray.com/bertrandmartel/maven/bboxapi-router/_latestVersion)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/fr.bmartel/bboxapi-router/badge.svg)](https://maven-badges.herokuapp.com/maven-central/fr.bmartel/bboxapi-router)
[![Javadoc](http://javadoc-badge.appspot.com/fr.bmartel/bboxapi-router.svg?label=javadoc)](http://javadoc-badge.appspot.com/fr.bmartel/bboxapi-router)
[![codecov](https://codecov.io/gh/bertrandmartel/bboxapi-router/branch/master/graph/badge.svg)](https://codecov.io/gh/bertrandmartel/bboxapi-router)
[![License](http://img.shields.io/:license-mit-blue.svg)](LICENSE.md)

[Bbox Router API](https://api.bbox.fr/doc/apirouter/index.html) client library for Kotlin/Java/Android

[**Go to Documentation**](http://bertrandmartel.github.io/bboxapi-router)

## Features

- [x] login (`POST /login`)
- [x] information summary (`GET /summary`)
- [x] voip data (`GET /voip`)
- [x] device information (`GET /device`)
- [x] call log (`GET /voip/fullcalllog/$line`)
- [x] known hosts (`GET /hosts`)
- [x] wireless info (`GET /wireless`)
- [x] set wifi state (`PUT /wireless?radio.enable=1`)
- [x] set display state (`PUT /device/display?luminosity=100`)
- [x] dial phone number (`PUT /voip/dial?line=$line&number=$num`)
- [x] reboot bbox (`POST /device/reboot?btoken=xxx`)
- [x] get xdsl information (`GET /wan/xdsl`)
- [x] get wan ip info (`GET /wan/ip`)
- [x] enable/disable wifi mac filter (`PUT /wireless/acl`)
- [x] get wifi mac filters (`GET /wireless/acl`)
- [x] create wifi mac filter (`POST /wireless/acl?btoken=xxx`)
- [x] update wifi mac filter (`PUT /wireless/acl/$rule`)
- [x] delete wifi mac filter (`DELETE /wireless/acl/$rule`)
- [x] start password recovery (`POST /password-recovery`)
- [x] verify password recovery (`GET /password-recovery/verify`)
- [x] reset password (`POST /reset-password`)
- [x] services list (`GET /services`)

## Tests

Run test on mockserver :
```bash
./gradlew test
```

## External Library

* [Fuel](https://github.com/kittinunf/Fuel)
* [Gson](https://github.com/google/gson)

## API documentation

https://api.bbox.fr/doc/apirouter/index.html

## License

The MIT License (MIT) Copyright (c) 2017-2018 Bertrand Martel
