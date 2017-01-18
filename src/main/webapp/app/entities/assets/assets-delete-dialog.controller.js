(function() {
    'use strict';

    angular
        .module('fuelbuddyApp')
        .controller('AssetsDeleteController',AssetsDeleteController);

    AssetsDeleteController.$inject = ['$uibModalInstance', 'entity', 'Assets'];

    function AssetsDeleteController($uibModalInstance, entity, Assets) {
        var vm = this;

        vm.assets = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Assets.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
