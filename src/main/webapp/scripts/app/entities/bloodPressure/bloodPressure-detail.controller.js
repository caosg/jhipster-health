'use strict';

angular.module('healthApp')
    .controller('BloodPressureDetailController', function ($scope, $rootScope, $stateParams, entity, BloodPressure, User) {
        $scope.bloodPressure = entity;
        $scope.load = function (id) {
            BloodPressure.get({id: id}, function(result) {
                $scope.bloodPressure = result;
            });
        };
        var unsubscribe = $rootScope.$on('healthApp:bloodPressureUpdate', function(event, result) {
            $scope.bloodPressure = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
