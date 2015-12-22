'use strict';

angular.module('healthApp').controller('BloodPressureDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'BloodPressure', 'User',
        function($scope, $stateParams, $uibModalInstance, entity, BloodPressure, User) {

        $scope.bloodPressure = entity;
        $scope.users = User.query();
        $scope.load = function(id) {
            BloodPressure.get({id : id}, function(result) {
                $scope.bloodPressure = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('healthApp:bloodPressureUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.bloodPressure.id != null) {
                BloodPressure.update($scope.bloodPressure, onSaveSuccess, onSaveError);
            } else {
                BloodPressure.save($scope.bloodPressure, onSaveSuccess, onSaveError);
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
