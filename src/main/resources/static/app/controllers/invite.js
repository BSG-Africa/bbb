angular.module('BigBlueButton')
    .controller('InviteController', function ($http, $scope, AuthService, $state, $stateParams, $location, $window, $timeout) {
        $scope.user = AuthService.user;

        $scope.name = '';
        $scope.meetingParam = $location.search().meetingID;

        $scope.submit = function () {
            $http.get('invite', {params:{"fullName": $scope.name, "meetingId": $scope.meetingParam}}).success(function (res) {
                $scope.message = '';
                if (res.meetingStatus === 'Started') {
                    navigateToURL(res.inviteURL);
                }else{
                    var tempUser = {principal : {name : res.fullName}};
                    console.log(tempUser);
                    $state.go('loading-invite', {user: tempUser});
                    $scope.user = tempUser;
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
                    }, 2000);
                }).error(function (error) {
                    $scope.message = error.message;
                });

            }

        };

        var navigateToURL = function (joinURL) {
            $window.location.href = joinURL;
        };

    });