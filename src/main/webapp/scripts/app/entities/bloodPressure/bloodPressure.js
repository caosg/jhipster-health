'use strict';

angular.module('healthApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('bloodPressure', {
                parent: 'entity',
                url: '/bloodPressures',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'healthApp.bloodPressure.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/bloodPressure/bloodPressures.html',
                        controller: 'BloodPressureController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('bloodPressure');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('bloodPressure.detail', {
                parent: 'entity',
                url: '/bloodPressure/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'healthApp.bloodPressure.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/bloodPressure/bloodPressure-detail.html',
                        controller: 'BloodPressureDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('bloodPressure');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'BloodPressure', function($stateParams, BloodPressure) {
                        return BloodPressure.get({id : $stateParams.id});
                    }]
                }
            })
            .state('bloodPressure.new', {
                parent: 'bloodPressure',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/bloodPressure/bloodPressure-dialog.html',
                        controller: 'BloodPressureDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    timestamp: null,
                                    systolic: null,
                                    diastolic: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('bloodPressure', null, { reload: true });
                    }, function() {
                        $state.go('bloodPressure');
                    })
                }]
            })
            .state('bloodPressure.edit', {
                parent: 'bloodPressure',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/bloodPressure/bloodPressure-dialog.html',
                        controller: 'BloodPressureDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['BloodPressure', function(BloodPressure) {
                                return BloodPressure.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('bloodPressure', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('bloodPressure.delete', {
                parent: 'bloodPressure',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/bloodPressure/bloodPressure-delete-dialog.html',
                        controller: 'BloodPressureDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['BloodPressure', function(BloodPressure) {
                                return BloodPressure.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('bloodPressure', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
