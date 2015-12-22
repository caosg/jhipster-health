'use strict';

angular.module('healthApp')
    .controller('PreferencesController', function ($scope, $state, Preferences, PreferencesSearch) {

        $scope.preferencess = [];
        $scope.loadAll = function() {
            Preferences.query(function(result) {
               $scope.preferencess = result;
            });
        };
        $scope.loadAll();


        $scope.search = function () {
            PreferencesSearch.query({query: $scope.searchQuery}, function(result) {
                $scope.preferencess = result;
            }, function(response) {
                if(response.status === 404) {
                    $scope.loadAll();
                }
            });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.preferences = {
                weekly_goal: null,
                weight_units: null,
                id: null
            };
        };
    });
