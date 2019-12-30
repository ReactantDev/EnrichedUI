rm -rf 0 1 2
mkdir 0 1 2

images=($1*.png)
images_count=${#images[@]}

for file in $1*.png; do
    name=${file%.png}
    prefix=$(sed -r 's/[0-9]*$//' <<< $name)
    number=${name#$prefix} # remove prefix
    number=$(expr $number + 0)
    echo $number
    if [ "$reverse" != false ]; then number=$((images_count - number)) ;fi
    convert $file \
        -strip \
        -crop 3x1@ +repage %d/$prefix$number.png
done;