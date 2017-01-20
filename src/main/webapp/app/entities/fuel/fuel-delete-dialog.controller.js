(function() {
    'use strict';

    angular
        .module('fuelbuddyApp')
        .controller('FuelDeleteController',FuelDeleteController);

    FuelDeleteController.$inject = ['$uibModalInstance', 'entity', 'Fuel'];

    function FuelDeleteController($uibModalInstance, entity, Fuel) {
        var vm = this;

        vm.fuel = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Fuel.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
