'use strict';

angular.module('healthApp')
    .controller('PreferencesDetailController', function ($scope, $rootScope, $stateParams, entity, Preferences, User) {
        $scope.preferences = entity;
        $scope.load = function (id) {
            Preferences.get({id: id}, function(result) {
                $scope.preferences = result;
            });
        };
        var unsubscribe = $rootScope.$on('healthApp:preferencesUpdate', function(event, result) {
            $scope.preferences = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
