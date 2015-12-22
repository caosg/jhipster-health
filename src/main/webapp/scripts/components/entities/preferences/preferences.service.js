'use strict';

angular.module('healthApp')
    .factory('Preferences', function ($resource, DateUtils) {
        return $resource('api/preferencess/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'user': { method: 'GET',isArray: false, url: '/api/my-preferences'},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });
