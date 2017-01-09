angular.module('BigBlueButton')
// Creating the Angular Controller
    .controller('PageNotFoundController', function ($http, $scope, AuthService, $rootScope) {
        if(AuthService.user.principal.name){
            $rootScope.$broadcast('PageNotFound');
        }
    });
