REQUEST_URL="127.0.0.1:8001"

echo "pwd ": $(pwd)
echo "project path: " ${PROJECT_DIR}

echo "connecting with devtools..."

response=$(curl -s -w "%{http_code}" -o response.txt $REQUEST_URL/miniappplugin/buildIOSArchive?scheme=$scheme)
status_code=${response: -3}
response_body=$(cat response.txt)

echo "status code: $status_code"
echo "response: $response_body"

rm response.txt

# 非 200 的情况都不阻塞 XCode 构建
if [[ $status_code != "200" ]]; then
    echo "connecting with devtools fail"
    echo "update ios resource fail"
else
  if [[ $response_body == "success" ]]; then
    echo "update ios resource done"
  else
    echo "update ios resource fail"
    exit 1
  fi
fi

echo "replace app resource..."

if [[ ${SDKROOT} == *"iPhoneSimulator"* ]]; then
    cp -rf ${PROJECT_DIR}/resources/x86/demo.app ${BUILT_PRODUCTS_DIR}
else
    cp -rf ${PROJECT_DIR}/resources/arm64/demo.app ${BUILT_PRODUCTS_DIR}
fi
