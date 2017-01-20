(function() {
    'use strict';

    angular
        .module('fuelbuddyApp')
        .controller('FuelDialogController', FuelDialogController);

    FuelDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Fuel'];

    function FuelDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Fuel) {
        var vm = this;

        vm.fuel = entity;
        vm.clear = clear;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.fuel.id !== null) {
                Fuel.update(vm.fuel, onSaveSuccess, onSaveError);
            } else {
                Fuel.save(vm.fuel, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('fuelbuddyApp:fuelUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
