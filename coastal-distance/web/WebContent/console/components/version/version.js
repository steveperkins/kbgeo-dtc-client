'use strict';

angular.module('kbconsole.version', [
  'kbconsole.version.interpolate-filter',
  'kbconsole.version.version-directive'
])

.value('version', '0.1');
