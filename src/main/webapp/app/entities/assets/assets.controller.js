(function() {
    'use strict';

    angular
        .module('fuelbuddyApp')
        .controller('AssetsController', AssetsController);

    AssetsController.$inject = ['$scope', '$state', 'Assets', 'AssetsSearch'];

    function AssetsController ($scope, $state, Assets, AssetsSearch) {
        var vm = this;

        vm.assets = [];
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Assets.query(function(result) {
                vm.assets = result;
                vm.searchQuery = null;
            });
        }

        function search() {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            AssetsSearch.query({query: vm.searchQuery}, function(result) {
                vm.assets = result;
                vm.currentSearch = vm.searchQuery;
            });
        }

        function clear() {
            vm.searchQuery = null;
            loadAll();
        }    }
})();
