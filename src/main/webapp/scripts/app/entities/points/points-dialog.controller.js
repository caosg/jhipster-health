'use strict';

angular.module('healthApp').controller('PointsDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Points', 'User',
        function($scope, $stateParams, $uibModalInstance, entity, Points, User) {

        $scope.points = entity;
        $scope.users = User.query();
        $scope.load = function(id) {
            Points.get({id : id}, function(result) {
                $scope.points = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('healthApp:pointsUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.points.id != null) {
                Points.update($scope.points, onSaveSuccess, onSaveError);
            } else {
                Points.save($scope.points, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.datePickerForDate = {};

        $scope.datePickerForDate.status = {
            opened: false
        };

        $scope.datePickerForDateOpen = function($event) {
            $scope.datePickerForDate.status.opened = true;
        };
}]);
