./gulp.sh
fswatch -o . -E -e "assets|welcome.html" | xargs -n 1 ./gulp.sh > gulp.log 2>&1 &
