(function() {
    'use strict';

    angular
        .module('fuelbuddyApp')
        .factory('AssetsSearch', AssetsSearch);

    AssetsSearch.$inject = ['$resource'];

    function AssetsSearch($resource) {
        var resourceUrl =  'api/_search/assets/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
