'use strict';

angular.module('healthApp').controller('WeightDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Weight', 'User',
        function($scope, $stateParams, $uibModalInstance, entity, Weight, User) {

        $scope.weight = entity;
        $scope.users = User.query();
        $scope.load = function(id) {
            Weight.get({id : id}, function(result) {
                $scope.weight = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('healthApp:weightUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.weight.id != null) {
                Weight.update($scope.weight, onSaveSuccess, onSaveError);
            } else {
                Weight.save($scope.weight, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.datePickerForTimestamp = {};

        $scope.datePickerForTimestamp.status = {
            opened: false
        };

        $scope.datePickerForTimestampOpen = function($event) {
            $scope.datePickerForTimestamp.status.opened = true;
        };
}]);
