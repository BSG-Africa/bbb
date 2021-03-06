angular.module('BigBlueButton').config(function ($stateProvider, $urlRouterProvider) {
    $urlRouterProvider.otherwise('/page-not-found');
    $stateProvider.state('nav', {
        abstract: true,
        url: '',
        views: {
            'nav@': {
                templateUrl: 'app/views/nav.html',
                controller: 'NavController'
            }
        }
    }).state('login', {
        parent: 'nav',
        url: '/login',
        views: {
            'content@': {
                templateUrl: 'app/views/login.html',
                controller: 'LoginController'
            }
        }
    }).state('meeting', {
        parent: 'nav',
        url: '/meeting',
        views: {
            'content@': {
                templateUrl: 'app/views/meeting.html',
                controller: 'MeetingController'
            }
        }
    }).state('loading-meeting', {
        parent: 'nav',
        url: '/loading-meeting/:meetingName',
        views: {
            'content@': {
                templateUrl: 'app/views/loading.html',
                controller: 'MeetingController'
            }
        },
        params:{
            'meetingName': ''
        }
    }).state('loading-invite', {
        parent: 'nav',
        url: '/loading-invite',
        views: {
            'content@': {
                templateUrl: 'app/views/loading.html',
                controller: 'InviteController'
            }
        },
        params:{
            'fullName': '',
            'meetingName': ''
        }
    }).state('users', {
        parent: 'nav',
        url: '/users',
        data: {
            role: 'ADMIN'
        },
        views: {
            'content@': {
                templateUrl: 'app/views/users.html',
                controller: 'UsersController',
            }
        }
    }).state('page-not-found', {
        parent: 'nav',
        url: '/page-not-found',
        views: {
            'content@': {
                templateUrl: 'app/views/page-not-found.html',
                controller: 'PageNotFoundController'
            }
        }
    }).state('access-denied', {
        parent: 'nav',
        url: '/access-denied',
        views: {
            'content@': {
                templateUrl: 'app/views/access-denied.html',
                controller: 'AccessDeniedController'
            }
        }
    }).state('register', {
        parent: 'nav',
        url: '/register',
        views: {
            'content@': {
                templateUrl: 'app/views/register.html',
                controller: 'RegisterController'
            }
        }
    }).state('invite', {
        parent: 'nav',
        url: '/invite',
        views: {
            'content@': {
                templateUrl: 'app/views/invite.html',
                controller: 'InviteController'
            }
        }
    });
});
