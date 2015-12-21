'use strict';

angular.module('healthApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


