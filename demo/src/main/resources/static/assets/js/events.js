 fetch("http://localhost:8001/api/events/")
    .then((res) => res.json())
    .then((res) => {
        console.log(res);
    })
    .catch((e) => {
        console.log(res);
    })