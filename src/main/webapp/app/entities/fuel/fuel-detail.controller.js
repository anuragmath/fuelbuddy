(function() {
    'use strict';

    angular
        .module('fuelbuddyApp')
        .controller('FuelDetailController', FuelDetailController);

    FuelDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Fuel'];

    function FuelDetailController($scope, $rootScope, $stateParams, previousState, entity, Fuel) {
        var vm = this;

        vm.fuel = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('fuelbuddyApp:fuelUpdate', function(event, result) {
            vm.fuel = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
