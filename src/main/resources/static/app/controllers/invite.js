angular.module('BigBlueButton')
    .controller('InviteController', function ($rootScope, $http, $scope, AuthService, $state, $stateParams, $location, $window, $timeout) {
        /**
         * Hide register menu while user accessing invite or invite is loading
         */
        if($state.current.name == 'invite' || $state.current.name == 'loading-invite'){
            $rootScope.$broadcast('RegisterAndLoginNotAllowed');
        }

        $scope.user = AuthService.user;
        $scope.name = $stateParams.fullName;
        $scope.meetingName = $stateParams.meetingName;
        $scope.meetingParam = $location.search().meetingID;

        /**
        * This function gets meeting invite details and checks if the meeting has started,
        * by sending get http request containing mapping and parameter.
        * If the meeting has started it redirects user to the bbb meeting.
        * If meeting has not started user gets redirected to an invite loading page
        *
        * param fullName - which is the bbb meeting full name
        * param meetingId - which is the bbb meeting Id
        */
        $scope.submit = function () {
            $http.get('invite', {params:{"fullName": $scope.name, "meetingId": $scope.meetingParam}})
                .success(function (res) {
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

        /**
         * This function frequently checks if the meeting has started and redirects meeting invite based
         * on meeting status.
         * If the meeting has started the user is redirected to the bbb meeting.
         * If meeting has not started the redirectToMeeting function is called, then the process gets repeated
         * until meeting has started.
         *
         * @param meetingInvite - which is used to check bbb meeting status and redirecting to bbb meeting
         * @param joinURL - which is used when calling the redirect method
         * TODO : JoinURL is not used but continuously parsed as parameter
         *
         * mapping invite
         */
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

        /**
         * This function navigates to the provided join URL parsed as a parameter
         * by setting the js window location reference to the join url.
         *
         * @param joinURL - the url reference used to navigate to a new window
         */
        var navigateToURL = function (joinURL) {
            $window.location.href = joinURL;
        };

    });