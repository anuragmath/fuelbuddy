(function() {
    'use strict';
    angular
        .module('fuelbuddyApp')
        .factory('Fuel', Fuel);

    Fuel.$inject = ['$resource'];

    function Fuel ($resource) {
        var resourceUrl =  'api/fuels/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
