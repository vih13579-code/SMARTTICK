
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>About Us Page</title>
        <style>
            body {
                font-family: Arial, sans-serif;
                margin: 0;
                padding: 0;
                box-sizing: border-box;
                background-color: white;
            }
            .container {
                max-width: 1200px;
                margin: 0 auto;
                padding: 0px;
            }
            .content-black {
                display: flex;
                flex-wrap: wrap;
                justify-content: center;
                align-items: center;
                gap: 10px;
                background-color: black;
                height: 450px;
                padding: 20px;
            }
            .content-black .text {
                max-width: 35%;
            }
            .content-black .text h2 {
                color: white;
            }
            .content-black .text p {
                font-size: 14px;
                line-height: 1.3;
                color: white;
            }
            .content-black .image{
                width: 35%;
                height: 100%;
            }
            .content-black .image img {
                width: 100%;
                height: 100%;
                border-radius: 5px;
            }
            .content-white {
                background-color: white;
                display: flex;
                flex-wrap: wrap;
                gap: 10px;
                justify-content: center;
                align-items: center;
                height: 450px;
                padding: 20px;
            }

            .content-white .image{
                width: 35%;
                height: 100%;
            }
            .content-white .image img {
                width: 100%;
                height: 100%;
                border-radius: 5px;
            }
            .content-white .text {
                max-width: 30%;
            }
            .title{
                display: flex;
                align-items: baseline;
            }
            .title img{
                width: 50px;
                height: 50px;
                background-color: blue;
                padding: 10px;
                border-radius: 50% 50% 0px 50%;
            }
            .title h2{
                margin-left: 20px;
            }
            .content-white .text p {
                font-size: 14px;
                line-height: 1.3;
            }

            .testimonial-container {
                margin: 20px auto;
                background-color: white;
                border-radius: 10px;
                box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                max-width: 800px;
                padding: 20px 80px;
            }
            .order{
                display: flex;
            }

            .testimonial-container img{
                width: 40px;
                height: 40px;                    
            }
            .testimonial-container p {
                width: 90%;
                font-style: italic;
                color: #555;
                margin: 0 0 20px;
            }

            .testimonial-container .author {
                text-align: right;
                font-weight: bold;
                color: #333;
            }

            .page {
                display: flex;
                justify-content: space-between;
                align-items: center;
            }

            .testimonial-container .dots {
                margin-top: 15px;
                display: flex;
                justify-content: center;
                gap: 5px;
            }

            .testimonial-container .dots span {
                width: 10px;
                height: 10px;
                background-color: #ddd;
                border-radius: 50%;
                cursor: pointer;
            }

            .testimonial-container .dots span.active {
                background-color: #007bff;
            }

            .review-button {
                display: inline-block;
                margin-top: 15px;
                padding: 10px 20px;
                font-size: 14px;
                color: #007bff;
                text-decoration: none;
                border: 1px solid blue;
                border-radius: 20px;
                transition: all 0.3s ease;
            }

            .review-button:hover {
                background-color: #007bff;
                color: #fff;
            }
            .features {
                width: 100%;
                padding: 50px 0px;
                display: flex;
                flex-wrap: wrap;
                justify-content: center;
                background-color: white;
            }
            .feature-box {
                display: inline-block;
                text-align: center;
                padding: 10px 60px;
                width: 33.33%;
                max-width: 400px;
            }

            .feature-box img{
                width: 25%;
                height: auto;
                background-color: white;
                border-radius: 50%;
                margin: 10px;
            }

            .feature-box h3 {
                font-size: 18px;
                font-weight: bold;
                margin-bottom: 10px;
                color: #333;
            }
            .feature-box p {
                font-size: 14px;
                color: #666;
            }
            @media screen and (max-width: 600px) {
                .content-black{
                    display: block;
                    height: auto;
                }
                .content-black .text {
                    max-width: 100%;
                }
                .content-black .image{
                    text-align: center;
                    width: 100%;
                }
                .content-black .image img {
                    width: 80%;
                }
                .content-white{
                    display: block;
                    height: auto;
                }
                .content-white .text {
                    max-width: 100%;
                }
                .content-white .image{
                    text-align: center;
                    width: 100%;
                }
                .content-white .image img {
                    width: 80%;
                }


                .feature-box {
                    width: 100%;
                    max-width: none;
                }

            }
        </style>
    </head>
    <body>
        <jsp:include page="header.jsp"></jsp:include>
            <div class="container">
                <p>Home > About Us</p>
                <h2>About Us</h2>
            </div>
            <div class="content-black">
                <div class="text">
                    <h2>A Family That Keeps On Growing</h2>
                    <p>We always aim to please the home market, supplying great computers and hardware at great prices to non-corporate customers, through our large Melbourne CBD showroom and our online store.</p>
                    <p>Shop management approach fosters a strong customer service focus in our staff. We prefer to cultivate long-term client relationships rather than achieve quick sales, demonstrated in the measure of our long-term success.</p>
                </div>
                <div class="image">
                    <img src="./assets/imgs/icon/shop.jpg" alt="Store image">
                </div>
            </div>
            <div class="content-white">
                <div class="image">
                    <img src="./assets/imgs/icon/keyboard.jpg" alt="Key Board">
                </div>
                <div class="text">
                    <div class="title">
                        <img src="./assets/imgs/icon/box.png" alt="box"/>
                        <h2>Shop.com</h2>
                    </div>
                    <p>Shop.com is a proudly Australian owned, Melbourne based supplier of I.T. goods and services, operating since 1991. Our client base encompasses individuals, small business, corporate and government organisations. We provide complete business IT solutions, centred on high quality hardware and exceptional customer service.</p>
                </div>
            </div>
            <div class="content-black">
                <div class="text">
                    <div class="title">
                        <img src="./assets/imgs/icon/heart.png" alt="heart"/>
                        <h2>Now You're In Safe Hands</h2>
                    </div>
                    <p>Experience a 40% boost in computing from last generation. MSI Desktop equips the 10th Gen. Intel® Core™ i7 processor with the upmost computing power to bring you an unparalleled gaming experience.</p>
                    <p>*Performance compared to i7-9700. Specs varies by model.</p>
                </div>
                <div class="image">
                    <img src="./assets/imgs/icon/CPU01.jpg" alt="CPU01">
                </div>
            </div>
            <div class="content-white">
                <div class="image">
                    <img src="./assets/imgs/icon/CPU02.jpg" alt="CPU02">
                </div>
                <div class="text">
                    <div class="title">
                        <img src="./assets/imgs/icon/star.png" alt="star"/>
                        <h2>The Highest Quality of Products</h2>
                    </div>
                    <p>We guarantee the highest quality of the products we sell. Several decades of successful operation and millions of happy customers let us feel certain about that. Besides, all items we sell pass thorough quality control, so no characteristics mismatch can escape the eye of our professionals.</p>
                </div>
            </div>
            <div class="content-black">
                <div class="text">
                    <div class="title">
                        <img src="./assets/imgs/icon/truck.png" alt="truck"/>
                        <h2>We Deliver to Any Regions</h2>
                    </div>
                    <p>We deliver our goods all across Australia. No matter where you live, your order will be shipped in time and delivered right to your door or to any other location you have stated. The packages are handled with utmost care, so the ordered products will be handed to you safe and sound, just like you expect them to be.</p>
                </div>
                <div class="image">
                    <img src="./assets/imgs/icon/CPU03.jpg" alt="CPU03">
                </div>
            </div>
            <div class="testimonial-container">
                <div class="order">
                    <img src="./assets/imgs/icon/orther.jpg" alt="order"/>
                    <p>
                        "My first order arrived today in perfect condition. From the time I sent a question about the item to making the purchase, to the shipping and now the delivery, your company, Tecs, has stayed in touch. Such great service. I look forward to shopping on your site in the future and would highly recommend it."
                    </p>

                </div>
                <p class="author">- Tama Brown</p>
                <div class="page">
                    <a href="#" class="review-button">Leave Us A Review</a>
                    <div class="dots">
                        <span class="active"></span>
                        <span></span>
                        <span></span>
                        <span></span>
                    </div>
                </div>
            </div>
            <div class="features">
                <div class="feature-box">
                    <img src="${pageContext.request.contextPath}/assets/imgs/Products/watches/watch-placeholder.svg" alt="head_phone"/>
                    <h3>Product Support</h3>
                    <p>Up to 3 years on-site warranty available for your peace of mind.</p>
                </div>
                <div class="feature-box">
                    <img src="./assets/imgs/ShoppingCartImg/person.jpg" alt="personal"/>
                    <h3>Personal Account</h3>
                    <p>With big discounts, free delivery, and a dedicated support specialist.</p>
                </div>
                <div class="feature-box">
                    <img src="./assets/imgs/ShoppingCartImg/tag.jpg" alt="tag"/>
                    <h3>Amazing Savings</h3>
                    <p>Up to 70% off new products, you can be sure of the best price.</p>
                </div>
            </div>
        <jsp:include page="footer.jsp"></jsp:include>
    </body>
</html>
