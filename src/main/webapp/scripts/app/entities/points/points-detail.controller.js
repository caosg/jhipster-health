'use strict';

angular.module('healthApp')
    .controller('PointsDetailController', function ($scope, $rootScope, $stateParams, entity, Points, User) {
        $scope.points = entity;
        $scope.load = function (id) {
            Points.get({id: id}, function(result) {
                $scope.points = result;
            });
        };
        var unsubscribe = $rootScope.$on('healthApp:pointsUpdate', function(event, result) {
            $scope.points = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
