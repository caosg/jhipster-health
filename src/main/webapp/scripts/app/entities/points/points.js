'use strict';

angular.module('healthApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('points', {
                parent: 'entity',
                url: '/pointss',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'healthApp.points.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/points/pointss.html',
                        controller: 'PointsController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('points');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('points.detail', {
                parent: 'entity',
                url: '/points/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'healthApp.points.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/points/points-detail.html',
                        controller: 'PointsDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('points');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Points', function($stateParams, Points) {
                        return Points.get({id : $stateParams.id});
                    }]
                }
            })
            .state('points.new', {
                parent: 'points',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/points/points-dialog.html',
                        controller: 'PointsDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    date: null,
                                    exercise: null,
                                    meals: null,
                                    alcohol: null,
                                    notes: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('points', null, { reload: true });
                    }, function() {
                        $state.go('points');
                    })
                }]
            })
            .state('points.edit', {
                parent: 'points',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/points/points-dialog.html',
                        controller: 'PointsDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Points', function(Points) {
                                return Points.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('points', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('points.delete', {
                parent: 'points',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/points/points-delete-dialog.html',
                        controller: 'PointsDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['Points', function(Points) {
                                return Points.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('points', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
