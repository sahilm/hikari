#!/usr/bin/env bash
set -eo pipefail

watch 'mysql -uroot -h127.0.0.1 --database foobar_db -e "select * from stuff"'
