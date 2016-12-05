//*********** IMPORTS *****************
var gulp = require('gulp');
var sass = require('gulp-ruby-sass');
var gutil = require('gulp-util');
var rename = require("gulp-rename");
var map = require("map-stream");
var livereload = require("gulp-livereload");
var concat = require("gulp-concat");
var uglify = require('gulp-uglify');
var watch = require('gulp-watch');
global.errorMessage = '';

gulp.task('copy-lib-files', function () {
    gulp.src(
        [
            'node_modules/angular/angular.js',
            'node_modules/angular/angular.min.js',
            'node_modules/angular-ui-router/release/angular-ui-router.js',
            'node_modules/angular-ui-router/release/angular-ui-router.min.js',
            'node_modules/ng-table/dist/ng-table.css',
            'node_modules/ng-table/dist/ng-table.min.css',
            'node_modules/ng-table/dist/ng-table.js',
            'node_modules/ng-table/dist/ng-table.min.js',
            'node_modules/bootstrap/dist/css/bootstrap.css',
            'node_modules/bootstrap/dist/css/bootstrap.min.css',
            'node_modules/bootstrap/dist/js/bootstrap.js',
            'node_modules/bootstrap/dist/js/bootstrap.min.js',
            'node_modules/ng-dialog/js/ngDialog.js',
            'node_modules/ng-dialog/js/ngDialog.min.js',
            'node_modules/ng-dialog/css/ngDialog.css',
            'node_modules/ng-dialog/css/ngDialog.min.css',
            'node_modules/ng-dialog/css/ngDialog-theme-default.css',
            'node_modules/ng-dialog/js/ngDialog-theme-default.min.css',
            'node_modules/ng-dialog/js/ngDialog-theme-plain.css',
            'node_modules/ng-dialog/js/ngDialog-theme-plain.min.css',
            'node_modules/jquery/dist/jquery.js',
            'node_modules/jquery/dist/jquery.min.js'
        ])
        .pipe(gulp.dest('src/main/resources/static/app/lib/'));

    gulp.src(
        [
            'node_modules/bootstrap/dist/fonts/*.*'
        ])
        .pipe(gulp.dest('src/main/libTest'));
});

gulp.task('default', ['watch']);