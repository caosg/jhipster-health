'use strict';

angular.module('healthApp')
    .factory('PointsSearch', function ($resource) {
        return $resource('api/_search/pointss/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
