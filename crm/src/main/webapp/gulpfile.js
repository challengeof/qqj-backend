var gulp = require('gulp');
var gutil = require('gulp-util');
var concat = require('gulp-concat');
var rev = require('gulp-rev-mtime');
var rename = require("gulp-rename");

var templateCache = require('gulp-angular-templatecache');
 
gulp.task('default', function () {
    gulp.src('app/**/*.html')
        .pipe(templateCache({ module:'templatesCache', standalone:true, root:'app'}))
        .pipe(gulp.dest('assets/'))
        .on('end', function() {
                gulp.src(['app/**/*.js'])
                     	.pipe(concat('app.js'))
                     	.pipe(gulp.dest('assets/')).on('end', function() {
                            gulp.src(['index.html'])
                                    .pipe(rename("welcome.html"))
                                    .pipe(rev({}))
                                    .pipe(gulp.dest('.'));
                     	})
        })

});

gulp.task('auto', function () {
    gulp.watch('app/**/*', ['default']);
})