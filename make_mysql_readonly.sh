#!/usr/bin/env bash
set -eo pipefail

mysql -uroot -h127.0.0.1 -e 'set @@global.read_only = 1;'
