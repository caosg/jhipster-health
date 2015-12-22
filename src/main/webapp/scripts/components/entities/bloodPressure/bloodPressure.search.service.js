'use strict';

angular.module('healthApp')
    .factory('BloodPressureSearch', function ($resource) {
        return $resource('api/_search/bloodPressures/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
