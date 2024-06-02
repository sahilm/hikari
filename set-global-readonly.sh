#!/usr/bin/env bash
set -eo pipefail

case "$1" in
  "1")
    mysql -uroot -h127.0.0.1 -e 'set @@global.read_only = 1;'
    ;;
  "0")
    mysql -uroot -h127.0.0.1 -e 'set @@global.read_only = 0;'
    ;;
  *)
    echo "usage: ./set-global-readonly.sh [1|0]"
    exit 1
    ;;
esac


