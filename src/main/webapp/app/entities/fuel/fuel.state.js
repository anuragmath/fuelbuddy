(function() {
    'use strict';

    angular
        .module('fuelbuddyApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('fuel', {
            parent: 'entity',
            url: '/fuel?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Fuels'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/fuel/fuels.html',
                    controller: 'FuelController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
            }
        })
        .state('fuel-detail', {
            parent: 'entity',
            url: '/fuel/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Fuel'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/fuel/fuel-detail.html',
                    controller: 'FuelDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Fuel', function($stateParams, Fuel) {
                    return Fuel.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'fuel',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('fuel-detail.edit', {
            parent: 'fuel-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/fuel/fuel-dialog.html',
                    controller: 'FuelDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Fuel', function(Fuel) {
                            return Fuel.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('fuel.new', {
            parent: 'fuel',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/fuel/fuel-dialog.html',
                    controller: 'FuelDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                price: null,
                                location: null,
                                type: null,
                                status: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('fuel', null, { reload: 'fuel' });
                }, function() {
                    $state.go('fuel');
                });
            }]
        })
        .state('fuel.edit', {
            parent: 'fuel',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/fuel/fuel-dialog.html',
                    controller: 'FuelDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Fuel', function(Fuel) {
                            return Fuel.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('fuel', null, { reload: 'fuel' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('fuel.delete', {
            parent: 'fuel',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/fuel/fuel-delete-dialog.html',
                    controller: 'FuelDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Fuel', function(Fuel) {
                            return Fuel.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('fuel', null, { reload: 'fuel' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
