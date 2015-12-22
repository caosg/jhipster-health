'use strict';

angular.module('healthApp')
    .controller('MainController', function ($scope, Principal,Preferences) {
        Principal.identity().then(function(account) {
            $scope.account = account;
            $scope.isAuthenticated = Principal.isAuthenticated;
        });

        Points.thisWeek(function(data) {
            $scope.pointsThisWeek = data;
            $scope.pointsPercentage = (data.points / 21) * 100;
        });

        Preferences.user(function(data) {
            $scope.preferences = data;
        });
    });
