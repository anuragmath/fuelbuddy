(function() {
    'use strict';

    angular
        .module('fuelbuddyApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('assets', {
            parent: 'entity',
            url: '/assets',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Assets'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/assets/assets.html',
                    controller: 'AssetsController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('assets-detail', {
            parent: 'entity',
            url: '/assets/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Assets'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/assets/assets-detail.html',
                    controller: 'AssetsDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Assets', function($stateParams, Assets) {
                    return Assets.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'assets',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('assets-detail.edit', {
            parent: 'assets-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/assets/assets-dialog.html',
                    controller: 'AssetsDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Assets', function(Assets) {
                            return Assets.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('assets.new', {
            parent: 'assets',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/assets/assets-dialog.html',
                    controller: 'AssetsDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                assetType: null,
                                manufacturer: null,
                                model: null,
                                fuelType: null,
                                assetIdentifier: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('assets', null, { reload: 'assets' });
                }, function() {
                    $state.go('assets');
                });
            }]
        })
        .state('assets.edit', {
            parent: 'assets',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/assets/assets-dialog.html',
                    controller: 'AssetsDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Assets', function(Assets) {
                            return Assets.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('assets', null, { reload: 'assets' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('assets.delete', {
            parent: 'assets',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/assets/assets-delete-dialog.html',
                    controller: 'AssetsDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Assets', function(Assets) {
                            return Assets.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('assets', null, { reload: 'assets' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
