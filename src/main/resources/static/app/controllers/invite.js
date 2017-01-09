angular.module('BigBlueButton')
    .controller('InviteController', function ($rootScope, $http, $scope, AuthService, $state, $stateParams, $location, $window, $timeout) {
        if($state.current.name == 'invite' || $state.current.name == 'loading-invite'){
            $rootScope.$broadcast('RegisterNotAllowed');
        }

        $scope.user = AuthService.user;
        $scope.name = $stateParams.fullName;
        $scope.meetingName = $stateParams.meetingName;
        $scope.meetingParam = $location.search().meetingID;

        $scope.submit = function () {
            $http.get('invite', {params:{"fullName": $scope.name, "meetingId": $scope.meetingParam}}).success(function (res) {
                $scope.message = '';
                if (res.meetingStatus === 'Started') {
                    navigateToURL(res.inviteURL);
                }else{
                    $state.go('loading-invite', {fullName: $scope.name, meetingName: res.meetingName });
                    $scope.redirectToMeeting(res, res.inviteURL);
                }
            }).error(function (error) {
                $scope.message = error.message;
            });
        };

        $scope.redirectToMeeting = function (meetingInvite, joinURL) {
            if (meetingInvite.meetingStatus === 'Started') {
                navigateToURL(meetingInvite.inviteURL);
            }else{
                $http.get('invite', {params:{"fullName": $scope.name, "meetingId": $scope.meetingParam}}).success(function (res) {
                    $scope.message = '';
                    $timeout(function () {
                        $scope.redirectToMeeting(res, joinURL);
                    }, 4000);
                }).error(function (error) {
                    $scope.message = error.message;
                });

            }

        };

        var navigateToURL = function (joinURL) {
            $window.location.href = joinURL;
        };

    });