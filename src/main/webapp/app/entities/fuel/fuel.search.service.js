(function() {
    'use strict';

    angular
        .module('fuelbuddyApp')
        .factory('FuelSearch', FuelSearch);

    FuelSearch.$inject = ['$resource'];

    function FuelSearch($resource) {
        var resourceUrl =  'api/_search/fuels/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
