'use strict';

angular.module('healthApp')
	.controller('PreferencesDeleteController', function($scope, $uibModalInstance, entity, Preferences) {

        $scope.preferences = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            Preferences.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };

    });
