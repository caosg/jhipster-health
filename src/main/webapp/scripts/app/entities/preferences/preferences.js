'use strict';

angular.module('healthApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('preferences', {
                parent: 'entity',
                url: '/preferencess',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'healthApp.preferences.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/preferences/preferencess.html',
                        controller: 'PreferencesController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('preferences');
                        $translatePartialLoader.addPart('units');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('preferences.detail', {
                parent: 'entity',
                url: '/preferences/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'healthApp.preferences.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/preferences/preferences-detail.html',
                        controller: 'PreferencesDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('preferences');
                        $translatePartialLoader.addPart('units');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Preferences', function($stateParams, Preferences) {
                        return Preferences.get({id : $stateParams.id});
                    }]
                }
            })
            .state('preferences.new', {
                parent: 'preferences',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/preferences/preferences-dialog.html',
                        controller: 'PreferencesDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    weekly_goal: null,
                                    weight_units: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('preferences', null, { reload: true });
                    }, function() {
                        $state.go('preferences');
                    })
                }]
            })
            .state('preferences.edit', {
                parent: 'preferences',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/preferences/preferences-dialog.html',
                        controller: 'PreferencesDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Preferences', function(Preferences) {
                                return Preferences.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('preferences', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('preferences.delete', {
                parent: 'preferences',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/preferences/preferences-delete-dialog.html',
                        controller: 'PreferencesDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['Preferences', function(Preferences) {
                                return Preferences.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('preferences', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
