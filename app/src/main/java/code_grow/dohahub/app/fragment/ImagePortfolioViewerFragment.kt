package code_grow.dohahub.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import code_grow.dohahub.app.databinding.FragmentImagePortfolioViewerBinding
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem

class ImagePortfolioViewerFragment : Fragment() {
    private lateinit var binding: FragmentImagePortfolioViewerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentImagePortfolioViewerBinding.inflate(inflater, container, false)
        val args = ImagePortfolioViewerFragmentArgs.fromBundle(requireArguments())
        val position = args.position
        val portfolio = args.portfolio

        binding.carousel.registerLifecycle(lifecycle)

        val carouselList = mutableListOf<CarouselItem>()

        for (index in portfolio.indices) {
            if (index == position) {
                carouselList.add(0, CarouselItem(imageUrl = portfolio[index]))
            } else {
                carouselList.add(CarouselItem(imageUrl = portfolio[index]))
            }
        }

        binding.carousel.infiniteCarousel = carouselList.size > 1

        binding.carousel.setData(carouselList)


        return binding.root
    }
}