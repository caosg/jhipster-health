'use strict';

angular.module('healthApp')
    .controller('BloodPressureController', function ($scope, $state, BloodPressure, BloodPressureSearch, ParseLinks) {

        $scope.bloodPressures = [];
        $scope.predicate = 'id';
        $scope.reverse = true;
        $scope.page = 0;
        $scope.loadAll = function() {
            BloodPressure.query({page: $scope.page, size: 20, sort: [$scope.predicate + ',' + ($scope.reverse ? 'asc' : 'desc'), 'id']}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.bloodPressures.push(result[i]);
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 0;
            $scope.bloodPressures = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();


        $scope.search = function () {
            BloodPressureSearch.query({query: $scope.searchQuery}, function(result) {
                $scope.bloodPressures = result;
            }, function(response) {
                if(response.status === 404) {
                    $scope.loadAll();
                }
            });
        };

        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.bloodPressure = {
                timestamp: null,
                systolic: null,
                diastolic: null,
                id: null
            };
        };
    });
