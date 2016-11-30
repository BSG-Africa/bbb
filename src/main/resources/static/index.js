angular.module('myApp', [])
    .controller('indexCtrl', function ($scope) {
        $scope.User = {
            name: 'Session Invites',
            friends: [{
                'id': 1,
                'name': 'raju'
            }, {
                'id': 2,
                'name': 'radha'
            }, {
                'id': 3,
                'name': 'luttappi'
            }]
        };
        $scope.update = function (friend) {

            alert(friend.name);
        };

        function pushUserCredentials() {
            var username = document.forms["loginForm"]["user"].value;
            fieldsFilledOut(username);
            var password = document.forms["loginForm"]["password"].value;
            fieldsFilledOut(password);
            var userCred = {
                "username": username,
                "password": password
            }
            userCredentials(username, password);
//        $.get("/getUserDetails/" + userCred);
            // call user credentials here
        }

        function userCredentials(username, password) {
            var userCred = {
                username: username,
                password: password
            }
            alert(userCred)
            $.ajax({
                type: "POST",
                dataType: 'json',
                url: "/getUserDetails",
                data: userCred,
                success: function (response) {
                    window.location.href = "/index";
                },
                error: function (e) {
                    alert('Error: ' + e);
                }
            });
            alert("HERE")
        }

        function fieldsFilledOut(inputs) {
            if (inputs == "") {
                alert("Name must be filled out");
                return false;
            }
        }
    });