'use strict';

angular.module('healthApp').controller('PreferencesDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Preferences', 'User',
        function($scope, $stateParams, $uibModalInstance, entity, Preferences, User) {

        $scope.preferences = entity;
        $scope.users = User.query();
        $scope.load = function(id) {
            Preferences.get({id : id}, function(result) {
                $scope.preferences = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('healthApp:preferencesUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.preferences.id != null) {
                Preferences.update($scope.preferences, onSaveSuccess, onSaveError);
            } else {
                Preferences.save($scope.preferences, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
}]);
