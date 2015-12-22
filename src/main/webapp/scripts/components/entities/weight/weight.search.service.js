'use strict';

angular.module('healthApp')
    .factory('WeightSearch', function ($resource) {
        return $resource('api/_search/weights/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
