(function() {
    'use strict';

    angular
        .module('fuelbuddyApp')
        .controller('AssetsDetailController', AssetsDetailController);

    AssetsDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Assets', 'User'];

    function AssetsDetailController($scope, $rootScope, $stateParams, previousState, entity, Assets, User) {
        var vm = this;

        vm.assets = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('fuelbuddyApp:assetsUpdate', function(event, result) {
            vm.assets = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
