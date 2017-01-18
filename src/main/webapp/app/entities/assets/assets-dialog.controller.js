(function() {
    'use strict';

    angular
        .module('fuelbuddyApp')
        .controller('AssetsDialogController', AssetsDialogController);

    AssetsDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Assets', 'User'];

    function AssetsDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Assets, User) {
        var vm = this;

        vm.assets = entity;
        vm.clear = clear;
        vm.save = save;
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.assets.id !== null) {
                Assets.update(vm.assets, onSaveSuccess, onSaveError);
            } else {
                Assets.save(vm.assets, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('fuelbuddyApp:assetsUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
